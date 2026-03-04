import { useState, useEffect, useRef } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Mic, MicOff, Loader2, User } from 'lucide-react'
import { generateQuestions, startInterview, submitAnswer, completeInterview } from '../services/api'

function InterviewPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { resumeSummary, jobRole } = location.state || {}

  const [questions, setQuestions] = useState([])
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0)
  const [sessionId, setSessionId] = useState(null)
  const [isRecording, setIsRecording] = useState(false)
  const [transcript, setTranscript] = useState('')
  const [timeLeft, setTimeLeft] = useState(90)
  const [loading, setLoading] = useState(true)
  const [isSpeaking, setIsSpeaking] = useState(false)
  const [answers, setAnswers] = useState([])

  const mediaRecorderRef = useRef(null)
  const recognitionRef = useRef(null)
  const timerRef = useRef(null)

  useEffect(() => {
    if (!resumeSummary || !jobRole) {
      navigate('/upload')
      return
    }

    initializeInterview()
    
    return () => {
      if (timerRef.current) clearInterval(timerRef.current)
      stopRecording()
    }
  }, [])

  const initializeInterview = async () => {
    try {
      const questionsData = await generateQuestions(resumeSummary, jobRole)
      setQuestions(questionsData.questions || [])
      
      const session = await startInterview(1, jobRole, resumeSummary)
      setSessionId(session.sessionId)
      
      setLoading(false)
      
      setTimeout(() => {
        speakQuestion(questionsData.questions[0].questionText)
      }, 1000)
    } catch (error) {
      console.error('Failed to initialize interview:', error)
      alert('Failed to start interview. Please try again.')
      navigate('/job-selection', { state: { resumeSummary } })
    }
  }

  const speakQuestion = (text) => {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(text)
      utterance.rate = 0.9
      utterance.pitch = 1
      utterance.volume = 1
      
      utterance.onstart = () => setIsSpeaking(true)
      utterance.onend = () => setIsSpeaking(false)
      
      window.speechSynthesis.speak(utterance)
    }
  }

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      mediaRecorderRef.current = new MediaRecorder(stream)
      
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
      if (SpeechRecognition) {
        recognitionRef.current = new SpeechRecognition()
        recognitionRef.current.continuous = true
        recognitionRef.current.interimResults = true
        
        recognitionRef.current.onresult = (event) => {
          let interimTranscript = ''
          let finalTranscript = ''
          
          for (let i = event.resultIndex; i < event.results.length; i++) {
            const transcriptPiece = event.results[i][0].transcript
            if (event.results[i].isFinal) {
              finalTranscript += transcriptPiece + ' '
            } else {
              interimTranscript += transcriptPiece
            }
          }
          
          setTranscript(prev => prev + finalTranscript)
        }
        
        recognitionRef.current.start()
      }
      
      mediaRecorderRef.current.start()
      setIsRecording(true)
      
      timerRef.current = setInterval(() => {
        setTimeLeft(prev => {
          if (prev <= 1) {
            handleNextQuestion()
            return 90
          }
          return prev - 1
        })
      }, 1000)
    } catch (error) {
      console.error('Failed to start recording:', error)
      alert('Please allow microphone access to continue')
    }
  }

  const stopRecording = () => {
    if (mediaRecorderRef.current && isRecording) {
      mediaRecorderRef.current.stop()
      mediaRecorderRef.current.stream.getTracks().forEach(track => track.stop())
    }
    
    if (recognitionRef.current) {
      recognitionRef.current.stop()
    }
    
    if (timerRef.current) {
      clearInterval(timerRef.current)
    }
    
    setIsRecording(false)
  }

  const handleNextQuestion = async () => {
    stopRecording()
    
    const currentQuestion = questions[currentQuestionIndex]
    const answer = {
      questionId: currentQuestion.id,
      questionText: currentQuestion.questionText,
      transcript: transcript || 'No answer provided'
    }
    
    setAnswers(prev => [...prev, answer])
    
    try {
      await submitAnswer(sessionId, currentQuestion.id, transcript || 'No answer provided')
    } catch (error) {
      console.error('Failed to submit answer:', error)
    }
    
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(prev => prev + 1)
      setTranscript('')
      setTimeLeft(90)
      
      setTimeout(() => {
        speakQuestion(questions[currentQuestionIndex + 1].questionText)
      }, 1000)
    } else {
      handleCompleteInterview()
    }
  }

  const handleCompleteInterview = async () => {
    try {
      await completeInterview(sessionId)
      navigate(`/report/${sessionId}`)
    } catch (error) {
      console.error('Failed to complete interview:', error)
      alert('Failed to complete interview. Please try again.')
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="w-16 h-16 text-blue-600 animate-spin mx-auto mb-4" />
          <p className="text-xl text-gray-700">Preparing your interview...</p>
        </div>
      </div>
    )
  }

  const currentQuestion = questions[currentQuestionIndex]

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-8 px-4">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-xl shadow-lg p-8 mb-6">
          <div className="flex justify-between items-center mb-6">
            <div>
              <p className="text-sm text-gray-600">Question {currentQuestionIndex + 1} of {questions.length}</p>
              <div className="w-64 h-2 bg-gray-200 rounded-full mt-2">
                <div 
                  className="h-full bg-blue-600 rounded-full transition-all"
                  style={{ width: `${((currentQuestionIndex + 1) / questions.length) * 100}%` }}
                />
              </div>
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-600">Time Remaining</p>
              <p className={`text-3xl font-bold ${timeLeft <= 10 ? 'text-red-600' : 'text-blue-600'}`}>
                {Math.floor(timeLeft / 60)}:{(timeLeft % 60).toString().padStart(2, '0')}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-6 mb-8">
            <div className="w-24 h-24 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
              <User className="w-12 h-12 text-white" />
            </div>
            <div className="flex-1">
              <div className="bg-gray-100 rounded-lg p-4">
                <p className="text-lg text-gray-800">{currentQuestion?.questionText}</p>
              </div>
              {isSpeaking && (
                <p className="text-sm text-blue-600 mt-2 animate-pulse">AI is speaking...</p>
              )}
            </div>
          </div>

          <div className="mb-6">
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Your Answer:
            </label>
            <textarea
              value={transcript}
              onChange={(e) => setTranscript(e.target.value)}
              placeholder="Your answer will appear here as you speak..."
              className="w-full h-32 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
            />
          </div>

          <div className="flex gap-4">
            {!isRecording ? (
              <button
                onClick={startRecording}
                disabled={isSpeaking}
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-4 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:bg-gray-400"
              >
                <Mic className="w-5 h-5" />
                Start Recording
              </button>
            ) : (
              <button
                onClick={stopRecording}
                className="flex-1 bg-red-600 hover:bg-red-700 text-white font-semibold py-4 rounded-lg transition-colors flex items-center justify-center gap-2 animate-pulse"
              >
                <MicOff className="w-5 h-5" />
                Stop Recording
              </button>
            )}
            
            <button
              onClick={handleNextQuestion}
              disabled={!transcript && isRecording}
              className="flex-1 bg-green-600 hover:bg-green-700 text-white font-semibold py-4 rounded-lg transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {currentQuestionIndex < questions.length - 1 ? 'Next Question' : 'Complete Interview'}
            </button>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-lg p-6">
          <h3 className="font-semibold text-gray-800 mb-3">Interview Tips:</h3>
          <ul className="text-sm text-gray-600 space-y-2">
            <li>• Speak clearly and at a moderate pace</li>
            <li>• Structure your answers: Situation, Task, Action, Result</li>
            <li>• Use specific examples from your experience</li>
            <li>• Take a moment to think before answering</li>
          </ul>
        </div>
      </div>
    </div>
  )
}

export default InterviewPage
