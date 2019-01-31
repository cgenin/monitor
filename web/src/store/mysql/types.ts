export interface MysqlState {
  testConnectionLoading: boolean
  infoSchemas: InfoSchema[]
}

export interface InfoSchema {
  description: string
  executiontime: number
  installedrank: 1
  installedon: number
  script: string
  state: FlywayState
}

export interface InfoSchemaDto {
  dt: string
  strState: string
}

export interface FlywayState {
  applied: boolean
  failed: boolean
  name: string
  resolved: boolean

}

export interface FailureConnexionDb {
  msgError?: string
  stacktrace?: string
  state: 'fail' | 'success'
}

export interface CreationSchemaResult {
  creation: boolean
  report?:Report
}

export interface Report {
  nbMigration: number
}

