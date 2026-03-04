import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Loader2, Award, TrendingUp, AlertCircle, CheckCircle, Home } from 'lucide-react'
import { RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, Radar, ResponsiveContainer } from 'recharts'
import { getReport } from '../services/api'

function ReportPage() {
  const { sessionId } = useParams()
  const navigate = useNavigate()
  const [report, setReport] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchReport()
  }, [sessionId])

  const fetchReport = async () => {
    try {
      const data = await getReport(sessionId)
      setReport(data)
    } catch (error) {
      console.error('Failed to fetch report:', error)
      alert('Failed to load report. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="w-16 h-16 text-blue-600 animate-spin mx-auto mb-4" />
          <p className="text-xl text-gray-700">Generating your performance report...</p>
        </div>
      </div>
    )
  }

  const radarData = [
    { subject: 'Technical', score: report?.technicalScore || 0, fullMark: 100 },
    { subject: 'Communication', score: report?.communicationScore || 0, fullMark: 100 },
    { subject: 'Confidence', score: report?.confidenceScore || 0, fullMark: 100 },
    { subject: 'Structure', score: report?.structureScore || 0, fullMark: 100 },
    { subject: 'Relevance', score: report?.relevanceScore || 0, fullMark: 100 }
  ]

  // Calculate answered vs skipped questions
  const totalQuestions = report?.questionFeedback?.length || 0
  const answeredQuestions = report?.questionFeedback?.filter(q => q.score > 0).length || 0
  const skippedQuestions = totalQuestions - answeredQuestions

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">Interview Performance Report</h1>
          <p className="text-gray-600">Detailed analysis and improvement roadmap</p>
        </div>

        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <ScoreCard
            title="Overall Score"
            score={report?.overallScore || 0}
            icon={<Award className="w-8 h-8" />}
            color="blue"
          />
          <ScoreCard
            title="Technical Score"
            score={report?.technicalScore || 0}
            icon={<TrendingUp className="w-8 h-8" />}
            color="green"
          />
          <ScoreCard
            title="Communication"
            score={report?.communicationScore || 0}
            icon={<CheckCircle className="w-8 h-8" />}
            color="purple"
          />
        </div>

        {skippedQuestions > 0 && (
          <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-6 mb-8">
            <div className="flex items-start gap-3">
              <AlertCircle className="w-6 h-6 text-yellow-600 flex-shrink-0 mt-1" />
              <div>
                <h3 className="font-semibold text-yellow-800 mb-2">Interview Completion Notice</h3>
                <p className="text-yellow-700">
                  You answered <span className="font-bold">{answeredQuestions} out of {totalQuestions}</span> questions.
                  {skippedQuestions > 0 && (
                    <span> {skippedQuestions} question{skippedQuestions > 1 ? 's were' : ' was'} skipped or not answered.</span>
                  )}
                </p>
                <p className="text-sm text-yellow-600 mt-2">
                  💡 Tip: Answering all questions provides a more accurate assessment of your skills.
                </p>
              </div>
            </div>
          </div>
        )}

        <div className="grid md:grid-cols-2 gap-8 mb-8">
          <div className="bg-white rounded-xl shadow-lg p-6">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">Performance Radar</h2>
            <ResponsiveContainer width="100%" height={300}>
              <RadarChart data={radarData}>
                <PolarGrid />
                <PolarAngleAxis dataKey="subject" />
                <PolarRadiusAxis angle={90} domain={[0, 100]} />
                <Radar
                  name="Your Score"
                  dataKey="score"
                  stroke="#3b82f6"
                  fill="#3b82f6"
                  fillOpacity={0.6}
                />
              </RadarChart>
            </ResponsiveContainer>
          </div>

          <div className="bg-white rounded-xl shadow-lg p-6">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">Key Insights</h2>
            <div className="space-y-4">
              <InsightItem
                icon={<CheckCircle className="w-6 h-6 text-green-600" />}
                title="Strengths"
                items={report?.strengths || ['Good technical knowledge', 'Clear communication']}
              />
              <InsightItem
                icon={<AlertCircle className="w-6 h-6 text-orange-600" />}
                title="Areas to Improve"
                items={report?.weaknesses || ['Answer structure', 'Confidence in responses']}
              />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Question-wise Feedback</h2>
          <div className="space-y-4">
            {report?.questionFeedback?.map((item, index) => (
              <QuestionFeedback key={index} data={item} index={index} />
            )) || (
              <div className="space-y-4">
                <QuestionFeedback 
                  index={0}
                  data={{
                    question: "Tell me about your experience with Java",
                    score: 75,
                    feedback: "Good understanding of core concepts. Could improve with more specific examples."
                  }}
                />
                <QuestionFeedback 
                  index={1}
                  data={{
                    question: "Explain your most challenging project",
                    score: 82,
                    feedback: "Well-structured answer with clear problem-solution approach."
                  }}
                />
              </div>
            )}
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Personalized Study Roadmap</h2>
          <div className="space-y-3">
            {report?.improvementRoadmap?.map((item, index) => (
              <RoadmapItem key={index} item={item} index={index} />
            )) || (
              <>
                <RoadmapItem index={0} item="Review SOLID principles and design patterns" />
                <RoadmapItem index={1} item="Practice behavioral questions using STAR method" />
                <RoadmapItem index={2} item="Deep dive into Spring Boot microservices architecture" />
                <RoadmapItem index={3} item="Work on communication clarity and confidence" />
              </>
            )}
          </div>
        </div>

        <div className="flex gap-4">
          <button
            onClick={() => navigate('/')}
            className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-4 rounded-lg transition-colors flex items-center justify-center gap-2"
          >
            <Home className="w-5 h-5" />
            Back to Home
          </button>
          <button
            onClick={() => navigate('/upload')}
            className="flex-1 bg-green-600 hover:bg-green-700 text-white font-semibold py-4 rounded-lg transition-colors"
          >
            Take Another Interview
          </button>
        </div>
      </div>
    </div>
  )
}

function ScoreCard({ title, score, icon, color }) {
  const colorClasses = {
    blue: 'from-blue-500 to-blue-600',
    green: 'from-green-500 to-green-600',
    purple: 'from-purple-500 to-purple-600'
  }

  return (
    <div className={`bg-gradient-to-br ${colorClasses[color]} rounded-xl shadow-lg p-6 text-white`}>
      <div className="flex items-center justify-between mb-2">
        <h3 className="text-lg font-semibold">{title}</h3>
        {icon}
      </div>
      <p className="text-4xl font-bold">{score}/100</p>
      <div className="w-full bg-white bg-opacity-30 rounded-full h-2 mt-3">
        <div
          className="bg-white rounded-full h-2 transition-all"
          style={{ width: `${score}%` }}
        />
      </div>
    </div>
  )
}

function InsightItem({ icon, title, items }) {
  return (
    <div>
      <div className="flex items-center gap-2 mb-2">
        {icon}
        <h3 className="font-semibold text-gray-800">{title}</h3>
      </div>
      <ul className="ml-8 space-y-1">
        {items.map((item, index) => (
          <li key={index} className="text-sm text-gray-600">• {item}</li>
        ))}
      </ul>
    </div>
  )
}

function QuestionFeedback({ data, index }) {
  const getScoreColor = (score) => {
    if (score === 0) return 'text-red-600'
    if (score < 40) return 'text-orange-600'
    if (score < 70) return 'text-yellow-600'
    return 'text-green-600'
  }

  const getScoreLabel = (score) => {
    if (score === 0) return 'Not Answered'
    if (score < 40) return 'Needs Improvement'
    if (score < 70) return 'Good'
    return 'Excellent'
  }

  return (
    <div className={`border rounded-lg p-4 ${data.score === 0 ? 'border-red-300 bg-red-50' : 'border-gray-200'}`}>
      <div className="flex items-start justify-between mb-2">
        <div className="flex-1">
          <p className="font-semibold text-gray-800 mb-1">Q{index + 1}: {data.question}</p>
          <p className="text-sm text-gray-600">{data.feedback}</p>
        </div>
        <div className="ml-4 text-center">
          <div className={`text-2xl font-bold ${getScoreColor(data.score)}`}>
            {data.score}%
          </div>
          <div className={`text-xs ${getScoreColor(data.score)}`}>
            {getScoreLabel(data.score)}
          </div>
        </div>
      </div>
    </div>
  )
}

function RoadmapItem({ item, index }) {
  return (
    <div className="flex items-start gap-3">
      <div className="w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center font-semibold flex-shrink-0">
        {index + 1}
      </div>
      <p className="text-gray-700 pt-1">{item}</p>
    </div>
  )
}

export default ReportPage
