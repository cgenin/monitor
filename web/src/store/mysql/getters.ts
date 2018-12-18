import {GetterTree} from 'vuex';
import {formatDate} from 'quasar-framework/src/utils/date';
import {RootState} from "../types";
import {InfoSchemaDto, MysqlState} from "./types";

export const getters: GetterTree<MysqlState, RootState> = {

  testConnectionLoading: (state: MysqlState) => state.testConnectionLoading,
  events: (state: MysqlState) => state.infoSchemas
    .map((obj) => {
      const dt = (obj.installedon) ? formatDate(new Date(obj.installedon), 'YYYY-MM-DD HH:mm:ss') : '';
      const strState = obj.state.name;
      return {dt, strState, ...obj} as InfoSchemaDto;
    }),
}
