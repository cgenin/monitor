import {GetterTree} from 'vuex';
import {formatDate} from 'quasar-framework/src/utils/date';
import {RootState} from "../types";
import {FrontResume, FrontsState} from "./types";

const fmDate = (dt: number) => ((dt) ? formatDate(new Date(dt), 'YYYY-MM-DD HH:mm:ss') : '');

export const getters: GetterTree<FrontsState, RootState> = {
  resume(state) {
    return state.resume.map((obj) => {
      const name = `${obj.groupId}.${obj.artifactId}`;
      const lastUpdateDate = fmDate(obj.lastUpdate);
      const lastUpdateSnapshotDate = (obj.snapshotVersion) ? fmDate(obj.lastUpdateSnapshot) : null;
      const lastUpdateReleaseDate = (obj.releaseVersion) ? fmDate(obj.lastUpdateRelease) : null;
      const packageJsonTxt = JSON.stringify(obj.packageJson, null, 3);
      return {
        name, lastUpdateDate, lastUpdateSnapshotDate, lastUpdateReleaseDate, packageJsonTxt, ...obj,
      } as FrontResume;
    })
  },
  nbFronts: state => state.resume.length,

  services: state => state.services
    .map(r => ({
      label: r,
      value: r.toUpperCase(),
    })),

  fronts: state => state.fronts.map(o => `${o.groupId}.${o.artifactId}`),
};
