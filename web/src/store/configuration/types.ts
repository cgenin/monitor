export interface ConfiguationState {
  mysql: Mysql,
  javaFilters: string[]
  npmFilters: string[]
  ignoreJava: string[]
  moniThorUrl: string
}

export interface Mysql {
  database: string
  host: string
  password: string
  user: string
  port: number
}


