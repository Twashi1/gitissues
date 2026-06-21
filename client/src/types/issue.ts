export const IssueStatus = {
  ARCHIVED: 'ARCHIVED',
  COMPLETED: 'COMPLETED',
  IN_PROGRESS: 'IN_PROGRESS',
  UNASSIGNED: 'UNASSIGNED',
} as const

export type IssueStatus =
  typeof IssueStatus[keyof typeof IssueStatus]

export type Issue = {
  id: number
  title: string
  description: string
  status: IssueStatus
}

export type IssueCreateRequest = {
  title: string
  description: string
  status: IssueStatus
}

export type IssuePatchRequest = {
  title?: string
  description?: string
  status?: IssueStatus
}

export type IssuePatchVariables = {
  id: number
  request: IssuePatchRequest
}
