export interface MicroServiceState {
  tables: Table[]
  apis: Api[],
  projects: Project[],
  project: Project,
  versions: Version[],
}

export interface Table {
  latestUpdate: number
  name: string
}

export interface TableDto extends Table {
  latest: string
}

export interface Api {
  artifactId: string
  path: string
  params: string

}

export interface ApiDto extends Api {
  absolutePath: string
  queryParams: string[]
  latestUpdate: number
}

export interface Project {
  id: string
  name: string
  artifactId: string
  groupId: string
  snapshot: string
  release: string
  javaDeps: string[]
  npmDeps: string[]
  tables: string[]
  latestUpdate: number
}

export interface ProjectDto extends Project {
  destinationUrl: string
  npmDepsTootip: string
  latest: string
}

export interface Version {
  id: string
  name: string
  javaDeps: string[]
  tables: string[]
  apis: string[]
  changelog: string
  latestUpdate: number
  isSnapshot: boolean

}

export interface VersionDto extends Version {
  latest: string
}

export interface GroupProjects {
  [key: string]: number
}

export interface Pagination {
  nb: number
  page: number
}
