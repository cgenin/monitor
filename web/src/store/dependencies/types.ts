export interface DependenciesState {
  dependencies: Dependencies,
  resources: string[],
}

export interface Dependencies {

}

export class ResourceDto {
  public label: string;
  public sublabel: string;
  public value: string;

  constructor(label: string, sublabel = 'Java') {
    this.label = label;
    this.value = label;
    this.sublabel = sublabel;
  }
}
