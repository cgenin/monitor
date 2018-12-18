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

