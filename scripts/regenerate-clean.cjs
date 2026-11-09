const fs = require('fs');
const path = require('path');

function walk(dir) {
    let results = [];
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        const fullPath = path.join(dir, file);
        const stat = fs.statSync(fullPath);
        if (stat && stat.isDirectory()) {
            results = results.concat(walk(fullPath));
        } else if (file.endsWith('.java')) {
            results.push(fullPath);
        }
    });
    return results;
}

// 1. Fix SecurityConfig
const secFile = path.join(__dirname, '../backend/src/main/java/com/finova/config/SecurityConfig.java');
if (fs.existsSync(secFile)) {
    let s = fs.readFileSync(secFile, 'utf8');
    s = s.replace(/return http;\s*$/m, 'return http.build();');
    fs.writeFileSync(secFile, s, 'utf8');
}

// 2. Fix AnalyticsServiceImpl Row ambiguity and user methods
const anFile = path.join(__dirname, '../backend/src/main/java/com/finova/service/impl/AnalyticsServiceImpl.java');
if (fs.existsSync(anFile)) {
    let a = fs.readFileSync(anFile, 'utf8');
    a = a.replace(/\bRow\s+([a-zA-Z0-9_]+)\s*=/g, 'org.apache.poi.ss.usermodel.Row $1 =');
    fs.writeFileSync(anFile, a, 'utf8');
}

// 3. Process entities and dtos
const targetDirs = [
    path.join(__dirname, '../backend/src/main/java/com/finova/entity'),
    path.join(__dirname, '../backend/src/main/java/com/finova/dto')
];

let filesToProcess = [];
targetDirs.forEach(d => {
    if (fs.existsSync(d)) filesToProcess.push(...walk(d));
});

// Skip inner-class files we already manually perfected:
const skipFiles = [
    'DashboardAnalyticsResponse.java',
    'ExpenseSplitResponse.java',
    'ExpenseSplitRequest.java',
    'DebtResponse.java',
    'SavingsGoalResponse.java',
    'AiInsightsResponse.java',
    'ExpensePredictorResponse.java'
];

filesToProcess.forEach(filePath => {
    if (skipFiles.some(sf => filePath.endsWith(sf))) return;

    let content = fs.readFileSync(filePath, 'utf8');
    const classNameMatch = content.match(/public\s+(?:class|interface|enum)\s+(\w+)/);
    if (!classNameMatch) return;
    const className = classNameMatch[1];

    // Find class body start
    const classDeclIdx = content.indexOf(`public class ${className}`);
    if (classDeclIdx === -1) return;
    const openBraceIdx = content.indexOf('{', classDeclIdx);
    if (openBraceIdx === -1) return;

    const prefix = content.substring(0, openBraceIdx + 1);

    // Strip out all previously generated methods, constructors, and builders
    // We only keep annotations and field declarations
    const bodyRest = content.substring(openBraceIdx + 1);
    
    // Find all field lines
    const lines = bodyRest.split('\n');
    let fieldDeclarations = [];
    let fields = [];

    for (let line of lines) {
        // Strip trailing comment
        const cleanLine = line.replace(/\/\/.*$/, '').trim();
        // Stop if we hit any method or constructor
        if (cleanLine.startsWith('public ') && (cleanLine.includes('(') || cleanLine.includes('static'))) {
            break;
        }
        if (cleanLine.startsWith('@PreUpdate') || cleanLine.startsWith('public void preUpdate')) {
            break;
        }

        fieldDeclarations.push(line);

        const fieldMatch = cleanLine.match(/^private\s+(final\s+)?([A-Za-z0-9_<>,\s?]+?)\s+([a-zA-Z0-9_]+)(\s*=\s*[^;]+)?;$/);
        if (fieldMatch) {
            const isFinal = Boolean(fieldMatch[1]);
            const type = fieldMatch[2].trim();
            const name = fieldMatch[3].trim();
            fields.push({ isFinal, type, name });
        }
    }

    let code = prefix + '\n\n' + fieldDeclarations.join('\n') + '\n\n';

    // No-args constructor
    code += `    public ${className}() {}\n\n`;

    // All-args constructor
    if (fields.length > 0) {
        const params = fields.map(f => `${f.type} ${f.name}`).join(', ');
        code += `    public ${className}(${params}) {\n`;
        fields.forEach(f => {
            code += `        this.${f.name} = ${f.name};\n`;
        });
        code += `    }\n\n`;
    }

    // Getters and Setters
    fields.forEach(f => {
        const cap = f.name.charAt(0).toUpperCase() + f.name.slice(1);
        code += `    public ${f.type} get${cap}() {\n        return this.${f.name};\n    }\n\n`;
        if (f.type === 'Boolean' || f.type === 'boolean') {
            code += `    public ${f.type} is${cap}() {\n        return this.${f.name};\n    }\n\n`;
        }
        if (!f.isFinal) {
            code += `    public void set${cap}(${f.type} ${f.name}) {\n        this.${f.name} = ${f.name};\n    }\n\n`;
        }
    });

    // Builder pattern
    code += `    public static ${className}Builder builder() {\n        return new ${className}Builder();\n    }\n\n`;
    code += `    public static class ${className}Builder {\n`;
    fields.forEach(f => {
        code += `        private ${f.type} ${f.name};\n`;
    });
    code += `\n        public ${className}Builder() {}\n\n`;
    fields.forEach(f => {
        code += `        public ${className}Builder ${f.name}(${f.type} ${f.name}) {\n            this.${f.name} = ${f.name};\n            return this;\n        }\n\n`;
    });
    code += `        public ${className} build() {\n`;
    code += `            ${className} instance = new ${className}();\n`;
    fields.forEach(f => {
        if (!f.isFinal) {
            const cap = f.name.charAt(0).toUpperCase() + f.name.slice(1);
            code += `            instance.set${cap}(this.${f.name});\n`;
        }
    });
    code += `            return instance;\n`;
    code += `        }\n`;
    code += `    }\n`;

    // Re-add preUpdate if entity
    if (content.includes('preUpdate()')) {
        code += `\n    @PreUpdate\n    public void preUpdate() {\n        this.updatedAt = java.time.Instant.now();\n    }\n`;
    }

    code += '}\n';

    fs.writeFileSync(filePath, code, 'utf8');
    console.log(`Cleanly regenerated: ${path.basename(filePath)} (${fields.length} fields)`);
});
