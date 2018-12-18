import { SET_URL } from './mutations-type';

export const nameModule = 'moniThor';
// actions
export const getServiceForServer = 'getServiceForServer';
export const getNpmComponentInfos = 'getNpmComponentInfos';
export const loadNpmList = 'loadNpmList';
export const loadServers = 'loadServers';
export const pingServer = 'pingServer';

// getters
export const servers = 'servers';

// Mutations
export const setUrlMutation = `${nameModule}/${SET_URL}`;
