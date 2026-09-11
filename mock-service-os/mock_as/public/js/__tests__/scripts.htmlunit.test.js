import { readFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';
import { parse } from '@babel/parser';

const __dirname = dirname(fileURLToPath(import.meta.url));
const scriptsPath = join(__dirname, '..', 'scripts.js');
const source = readFileSync(scriptsPath, 'utf8');
const ast = parse(source, { sourceType: 'script' });

const UNSUPPORTED_NODE_TYPES = ['SpreadElement', 'TemplateLiteral', 'TaggedTemplateExpression'];

function findNodesByType(node, types, found = [], seen = new Set()) {
  if (!node || typeof node.type !== 'string' || seen.has(node)) return found;
  seen.add(node);
  if (types.includes(node.type)) found.push(node);
  for (const key in node) {
    if (key === 'loc' || key === 'start' || key === 'end' || key === 'range') continue;
    const value = node[key];
    if (Array.isArray(value)) {
      value.forEach((child) => findNodesByType(child, types, found, seen));
    } else if (value && typeof value === 'object') {
      findNodesByType(value, types, found, seen);
    }
  }
  return found;
}

function describeLocation(node) {
  return { type: node.type, line: node.loc.start.line, column: node.loc.start.column };
}

describe('scripts.js — HtmlUnit compatibility', () => {
  it("does not use any construct known to break HtmlUnit's JS engine", () => {
    // Given a script parsed by the headless browser
    // And the browser is not fully ES2015+ compatible
    // When it is parsed and scanned for invalid constructs
    const offenders = findNodesByType(ast.program, UNSUPPORTED_NODE_TYPES).map(describeLocation);

    // Then none of those constructs should be present
    expect(offenders).toEqual([]);
  });
});
