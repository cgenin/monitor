import {GetterTree} from 'vuex';
import {format, formatYYYYMMDDHHmm} from '../../Dates';
import {ms} from '../../Routes';
import {Api, ApiDto, MicroServiceState, Project, ProjectDto, TableDto, VersionDto} from "./types";
import {RootState} from "../types";

const sortApis = (a: ApiDto, b: ApiDto) => a.absolutePath.localeCompare(b.absolutePath);

const extractQueryParams = (path: string) => {
  if (!path || path.indexOf('&') === -1) {
    return [];
  }
  return path.replace(/^(.*)\?/, '')
    .split('&');
};


const depTootip = (attr: string[]) => {
  if (!attr || attr.length === 0) {
    return '<p>Aucune dépendance.</p>';
  }
  return attr.map(v => `<ul><li>${v}</li></ul>`).reduce((a, b) => a + b, '');
};

function toProjectSummary(l: Project[]): ProjectDto[] {
  if (!l) {
    return [];
  }
  return l.map((p) => {
      const destinationUrl = ms(`/projects/${p.id}`);
      const snapshot = p.snapshot || '-';
      const release = p.release || '-';
      const javaDeps = p.javaDeps || [];
      const npmDeps = p.npmDeps || [];
      const tables = p.tables || [];
      const npmDepsTootip = depTootip(p.npmDeps);
      const latest = formatYYYYMMDDHHmm(p.latestUpdate);
      return {...p, destinationUrl, snapshot, release, javaDeps, npmDeps, npmDepsTootip, latest, tables} as ProjectDto;
    }
  );
};


export const getters: GetterTree<MicroServiceState, RootState> = {
  versions(state): VersionDto[] {
    return state.versions
      .map((o) => {
        const latest = formatYYYYMMDDHHmm(o.latestUpdate);
        return {...o, latest} as VersionDto;
      });
  },
  nbTables: state => state.tables.length,
  nbApis: state => state.apis.length,
  nbProjects: state => state.projects.length,
  project: state => state.project,
  apis(state) {
    return state.apis
      .map((o: Api) => {
        const context = (o.artifactId || '').replace('-client', '');
        const absolutePath = `/${context}${o.path}`
          .replace(/\?(.*)$/, '');
        const queryParams = extractQueryParams(`/${context}${o.path}`);
        return {...o, absolutePath, queryParams} as ApiDto;
      })
      .sort(sortApis);
  },
  tables: state => state.tables
    .map((o) => {
      const latest = format(o.latestUpdate);
      return {...o, latest} as TableDto;
    })
    .sort((a, b) => a.name.localeCompare(b.name)),
  projects: state => toProjectSummary(state.projects),

};

