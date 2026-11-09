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

const javaFiles = walk(path.join(__dirname, '../backend/src/main/java'));

javaFiles.forEach(filePath => {
    let content = fs.readFileSync(filePath, 'utf8');
    if (!content.includes('lombok')) return;

    const classNameMatch = content.match(/public\s+(?:class|interface|enum)\s+(\w+)/);
    if (!classNameMatch) return;
    const className = classNameMatch[1];

    const hasSlf4j = content.includes('@Slf4j');
    const hasRequiredArgs = content.includes('@RequiredArgsConstructor');
    const hasDataOrGetter = /@(Data|Getter|Setter)/.test(content);
    const hasBuilder = content.includes('@Builder');
    const hasNoArgs = content.includes('@NoArgsConstructor');
    const hasAllArgs = content.includes('@AllArgsConstructor');

    // Remove lombok annotations
    content = content.replace(/import\s+lombok\.[^;]+;\s*\r?\n/g, '');
    content = content.replace(/@Slf4j\s*\r?\n/g, '');
    content = content.replace(/@RequiredArgsConstructor\s*\r?\n/g, '');
    content = content.replace(/@Data\s*\r?\n/g, '');
    content = content.replace(/@Getter\s*\r?\n/g, '');
    content = content.replace(/@Setter\s*\r?\n/g, '');
    content = content.replace(/@NoArgsConstructor\s*\r?\n/g, '');
    content = content.replace(/@AllArgsConstructor\s*\r?\n/g, '');
    content = content.replace(/@Builder(\.[A-Za-z]+)?\s*/g, '');

    // Extract fields
    // Match fields: private [final] [static] Type name [= value];
    const fieldRegex = /^\s*private\s+(?!static\s+final\s+org\.slf4j)(final\s+)?([A-Za-z0-9_<>,\s?]+?)\s+([a-zA-Z0-9_]+)(\s*=\s*[^;]+)?;\s*$/gm;
    let fields = [];
    let match;
    const searchContent = content;
    while ((match = fieldRegex.exec(searchContent)) !== null) {
        const isFinal = Boolean(match[1]);
        const type = match[2].trim();
        const name = match[3].trim();
        fields.push({ isFinal, type, name });
    }

    let injected = '\n';

    if (hasSlf4j) {
        injected += `    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(${className}.class);\n\n`;
    }

    if (hasRequiredArgs) {
        const finalFields = fields.filter(f => f.isFinal);
        if (finalFields.length > 0) {
            const params = finalFields.map(f => `${f.type} ${f.name}`).join(', ');
            injected += `    public ${className}(${params}) {\n`;
            finalFields.forEach(f => {
                injected += `        this.${f.name} = ${f.name};\n`;
            });
            injected += `    }\n\n`;
        }
    }

    if (hasNoArgs || hasDataOrGetter || hasBuilder) {
        injected += `    public ${className}() {}\n\n`;
    }

    if (hasAllArgs || hasBuilder) {
        if (fields.length > 0) {
            const params = fields.map(f => `${f.type} ${f.name}`).join(', ');
            injected += `    public ${className}(${params}) {\n`;
            fields.forEach(f => {
                injected += `        this.${f.name} = ${f.name};\n`;
            });
            injected += `    }\n\n`;
        }
    }

    // Getters and Setters
    if (hasDataOrGetter || hasBuilder) {
        fields.forEach(f => {
            const cap = f.name.charAt(0).toUpperCase() + f.name.slice(1);
            // Getter
            injected += `    public ${f.type} get${cap}() {\n        return this.${f.name};\n    }\n\n`;
            if (f.type === 'Boolean' || f.type === 'boolean') {
                injected += `    public ${f.type} is${cap}() {\n        return this.${f.name};\n    }\n\n`;
            }
            // Setter
            if (!f.isFinal) {
                injected += `    public void set${cap}(${f.type} ${f.name}) {\n        this.${f.name} = ${f.name};\n    }\n\n`;
            }
        });
    }

    // Builder pattern if @Builder was used
    if (hasBuilder) {
        injected += `    public static ${className}Builder builder() {\n        return new ${className}Builder();\n    }\n\n`;
        injected += `    public static class ${className}Builder {\n`;
        fields.forEach(f => {
            injected += `        private ${f.type} ${f.name};\n`;
        });
        injected += `\n        public ${className}Builder() {}\n\n`;
        fields.forEach(f => {
            injected += `        public ${className}Builder ${f.name}(${f.type} ${f.name}) {\n            this.${f.name} = ${f.name};\n            return this;\n        }\n\n`;
        });
        injected += `        public ${className} build() {\n`;
        injected += `            ${className} instance = new ${className}();\n`;
        fields.forEach(f => {
            injected += `            instance.set${f.name.charAt(0).toUpperCase() + f.name.slice(1)}(this.${f.name});\n`;
        });
        injected += `            return instance;\n`;
        injected += `        }\n`;
        injected += `    }\n\n`;
    }

    // Insert before the last closing brace
    const lastBraceIdx = content.lastIndexOf('}');
    if (lastBraceIdx !== -1) {
        content = content.substring(0, lastBraceIdx) + injected + '}\n';
    }

    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`Delomboked: ${path.basename(filePath)}`);
});
