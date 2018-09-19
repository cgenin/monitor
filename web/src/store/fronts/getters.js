import { date } from 'quasar';

const formatDate = dt => ((dt) ? date.formatDate(new Date(dt), 'YYYY-MM-DD HH:mm:ss') : '');

export const resume = state => state.resume.map((obj) => {
  const name = `${obj.groupId}.${obj.artifactId}`;
  const lastUpdateDate = formatDate(obj.lastUpdate);
  const lastUpdateSnapshotDate = (obj.snapshotVersion) ? formatDate(obj.lastUpdateSnapshot) : null;
  const lastUpdateReleaseDate = (obj.releaseVersion) ? formatDate(obj.lastUpdateRelease) : null;
  const packageJsonTxt = JSON.stringify(obj.packageJson, null, 3);
  return {
    name, lastUpdateDate, lastUpdateSnapshotDate, lastUpdateReleaseDate, packageJsonTxt, ...obj,
  };
});
export const nbFronts = state => state.resume.length;

export const services = state => state.services
  .map(r => ({
    label: r,
    value: r.toUpperCase(),
  }));

export const fronts = state => state.fronts.map(o => `${o.groupId}.${o.artifactId}`);
