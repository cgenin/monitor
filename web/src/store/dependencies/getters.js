export const resources = state => state.resources.map(resource => ({
  label: resource.toLowerCase(),
  sublabel: 'Java',
  value: resource.toLowerCase(),
}));
export const dependencies = state => state.dependencies;
