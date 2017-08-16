import environment from '../environment';

export function getBaseUrl() {
  if(environment.local){
    return 'http://localhost:8888';
  }
  return '';
}
