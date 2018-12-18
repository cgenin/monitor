export interface FrontsState {
  resume: Fronts[],
  services: string[],
  fronts: Fronts[],
}

export interface Fronts {
  groupId: string
  artifactId: string
  lastUpdate: number
  lastUpdateSnapshot: number
  lastUpdateRelease: number
  snapshotVersion?: string
  releaseVersion?: string
  packageJson: any
}

export interface FrontResume extends Fronts {
  name:string
  lastUpdateDate:string
  lastUpdateSnapshotDate:string
  lastUpdateReleaseDate:string
  packageJsonTxt:string
}
