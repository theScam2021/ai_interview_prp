import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import ResumeUploadPage from './pages/ResumeUploadPage'
import JobSelectionPage from './pages/JobSelectionPage'
import InterviewPage from './pages/InterviewPage'
import ReportPage from './pages/ReportPage'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/upload" element={<ResumeUploadPage />} />
        <Route path="/job-selection" element={<JobSelectionPage />} />
        <Route path="/interview" element={<InterviewPage />} />
        <Route path="/report/:sessionId" element={<ReportPage />} />
      </Routes>
    </Router>
  )
}

export default App
