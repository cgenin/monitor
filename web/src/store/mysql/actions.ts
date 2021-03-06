import {ActionTree} from 'vuex';
import {SET_INFO_SCHEMA, UPDATE_LOADING} from './mutation-type';
import {createSchema, infoSchema, migrateEvents, startOrStop, test} from './constants';
import {CreationSchemaResult, FailureConnexionDb, InfoSchema, MysqlState} from "./types";
import {RootState} from "../types";
import {Mysql} from "../configuration/types";

export const actions: ActionTree<MysqlState, RootState> = {
  [startOrStop]() {
    return fetch('/api/configuration/db/mysql', {
      method: 'POST',
    });
  },
  [test]({commit}, mysql: Mysql): Promise<FailureConnexionDb> {
    commit(UPDATE_LOADING, true);
    const body = JSON.stringify(mysql);
    return fetch('/api/configuration/db/mysql/connect', {
      method: 'POST',
      body,
    })
      .then(res => res.json())
      .then((res) => {
        commit(UPDATE_LOADING, false);
        return res;
      })
      .catch((err) => {
        commit(UPDATE_LOADING, false);
        return err;
      });
  },
  [infoSchema]({commit}) {
    return fetch('/api/configuration/db/mysql')
      .then(res => res.json())
      .then((content) => {
        commit(SET_INFO_SCHEMA, <InfoSchema[]>content);
        return content;
      });
  },
  [createSchema]():Promise<CreationSchemaResult> {
    return fetch('/api/configuration/db/mysql/schemas', {
      method: 'PUT',
    })
      .then(res => res.json());
  },
  [migrateEvents]() {
    return fetch('/api/configuration/db/events/store', {
      method: 'POST',
    })
      .then(res => res.json());
  },
};
