export const CONTEXT_ROUTE = '/rt';
export const CONTEXT_MS = '/rt/ms';
export const CONTEXT_FRONT = '/rt/app';


export const path = p => `${CONTEXT_ROUTE}${p}`;
export const ms = p => `${CONTEXT_MS}${p}`;
export const front = p => `${CONTEXT_FRONT}${p}`;
export const isMicroService = $route => $route.path.indexOf(CONTEXT_MS) !== -1;
export const isFront = $route => $route.path.indexOf(CONTEXT_FRONT) !== -1;

// List of routes
export const Welcome = path('/welcome');
export const ProjectsList = ms('/projects-list');
export const Tables = ms('/tables');
export const TablesList = ms('/tables/list');
export const TablesCharts = ms('/tables/chart');
export const ApisList = ms('/apis-list');
export const Dependencies = ms('/dependencies');
export const FrontList = front('/list');
export const FrontDependencies = front('/dependencies');
