import { format, formatYYYYMMDDHHmm } from '../../Dates';
import { ms } from '../../Routes';

const sortApis = (a, b) => a.absolutePath.localeCompare(b.absolutePath);

const extractQueryParams = (path) => {
  if (!path || path.indexOf('&') === -1) {
    return [];
  }
  return path.replace(/^(.*)\?/, '')
    .split('&');
};

export const tables = state => state.tables
  .map((o) => {
    const latest = format(o.latestUpdate);
    return Object.assign({}, o, { latest });
  })
  .sort((a, b) => a.name.localeCompare(b.name));

export const nbTables = state => state.tables.length;
export const nbApis = state => state.apis.length;
export const nbProjects = state => state.projects.length;

export const apis = state => state.apis
  .map((o) => {
    const context = (o.artifactId || '')
      .replace('-impl', '');
    const absolutePath = `/${context}${o.path}`
      .replace(/\?(.*)$/, '');
    const queryParams = extractQueryParams(`/${context}${o.path}`);
    return Object.assign({}, o, { absolutePath, queryParams });
  })
  .sort(sortApis);

const depTootip = (attr) => {
  if (!attr || attr.length === 0) {
    return '<p>Aucune dépendance.</p>';
  }
  return attr.map(v => `<ul><li>${v}</li></ul>`).reduce((a, b) => a + b, '');
};
const toProjectSummary = (l) => {
  if (!l) {
    return [];
  }
  return l.map((p) => {
    p.destinationUrl = ms(`/projects/${p.id}`);
    p.snapshot = p.snapshot || '-';
    p.release = p.release || '-';
    p.javaDeps = p.javaDeps || [];
    p.npmDeps = p.npmDeps || [];
    p.tables = p.tables || [];
    p.npmDepsTootip = depTootip(p.npmDeps);
    p.latest = formatYYYYMMDDHHmm(p.latestUpdate);
    return p;
  });
};

export const projects = state => toProjectSummary(state.projects);
export const project = state => state.project;
export const versions = state => state.versions
  .map((o) => {
    const latest = formatYYYYMMDDHHmm(o.latestUpdate);
    return Object.assign({}, o, { latest });
  });
