const fs = require('fs');
const path = require('path');

// 1. Fix SecurityConfig.java
const secPath = path.join(__dirname, '../backend/src/main/java/com/finova/config/SecurityConfig.java');
let secContent = fs.readFileSync(secPath, 'utf8');
secContent = secContent.replace(/return http;\s*$/m, 'return http.build();');
fs.writeFileSync(secPath, secContent, 'utf8');
console.log('Fixed SecurityConfig.java');

// 2. Fix AnalyticsServiceImpl.java Row ambiguity
const analyticsPath = path.join(__dirname, '../backend/src/main/java/com/finova/service/impl/AnalyticsServiceImpl.java');
let anContent = fs.readFileSync(analyticsPath, 'utf8');
anContent = anContent.replace(/\bRow\s+([a-zA-Z0-9_]+)\s*=/g, 'org.apache.poi.ss.usermodel.Row $1 =');
fs.writeFileSync(analyticsPath, anContent, 'utf8');
console.log('Fixed AnalyticsServiceImpl.java');
