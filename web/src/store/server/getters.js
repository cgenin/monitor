export const health = state => state.health;
export const deletedCollections = state => state.deletedCollections;
export const nitrite = state => state.health.health[0] && state.health.health[0].db;

