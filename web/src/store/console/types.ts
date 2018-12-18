export interface ConsoleState {
  messages: Message[]
  isListening: boolean
}

export interface Message {
  msg: string
  date: number
  key?: string
  formattedDate?: string
  type?: string
}
