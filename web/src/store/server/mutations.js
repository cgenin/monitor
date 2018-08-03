import { SET_DELETED_COLLECTION, UPDATE } from './mutations-type';

export default {
  [UPDATE](state, health) {
    state.health = health;
  },
  [SET_DELETED_COLLECTION](state, deletedCollections) {
    state.deletedCollections = deletedCollections;
  },
};

