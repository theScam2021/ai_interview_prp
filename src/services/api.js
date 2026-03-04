import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const uploadResume = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  
  const response = await axios.post(`${API_BASE_URL}/resume/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  return response.data
}

export const generateQuestions = async (resumeSummary, jobRole) => {
  const response = await api.post('/interview/generate-questions', {
    resumeSummary,
    jobRole
  })
  return response.data
}

export const startInterview = async (userId, jobRole, resumeSummary) => {
  const response = await api.post('/interview/start', {
    userId,
    jobRole,
    resumeSummary
  })
  return response.data
}

export const submitAnswer = async (sessionId, questionId, transcript) => {
  const response = await api.post('/interview/submit-answer', {
    sessionId,
    questionId,
    transcript
  })
  return response.data
}

export const completeInterview = async (sessionId) => {
  const response = await api.post(`/interview/complete/${sessionId}`)
  return response.data
}

export const getReport = async (sessionId) => {
  const response = await api.get(`/interview/report/${sessionId}`)
  return response.data
}

export default api
