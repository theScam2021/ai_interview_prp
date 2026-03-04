import { useNavigate } from 'react-router-dom'
import { Sparkles, Target, TrendingUp, Award } from 'lucide-react'

function HomePage() {
  const navigate = useNavigate()

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-16">
        <div className="text-center mb-16">
          <h1 className="text-6xl font-bold text-gray-800 mb-4">
            Interview<span className="text-blue-600">AI</span>
          </h1>
          <p className="text-xl text-gray-600 mb-8">
            Realistic AI-Powered Mock Interview Simulator
          </p>
          <button
            onClick={() => navigate('/upload')}
            className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-4 rounded-lg text-lg transition-all transform hover:scale-105 shadow-lg"
          >
            Start Your Interview Preparation
          </button>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8 mt-16">
          <FeatureCard
            icon={<Sparkles className="w-12 h-12 text-blue-600" />}
            title="AI-Powered"
            description="Advanced AI analyzes your resume and generates personalized questions"
          />
          <FeatureCard
            icon={<Target className="w-12 h-12 text-purple-600" />}
            title="Job-Specific"
            description="Tailored interviews for different roles and skill levels"
          />
          <FeatureCard
            icon={<TrendingUp className="w-12 h-12 text-green-600" />}
            title="Real-Time Feedback"
            description="Get instant evaluation and improvement suggestions"
          />
          <FeatureCard
            icon={<Award className="w-12 h-12 text-yellow-600" />}
            title="Performance Reports"
            description="Detailed analytics and personalized study roadmap"
          />
        </div>

        <div className="mt-16 bg-white rounded-xl shadow-lg p-8">
          <h2 className="text-3xl font-bold text-gray-800 mb-6 text-center">
            How It Works
          </h2>
          <div className="grid md:grid-cols-4 gap-6">
            <Step number="1" title="Upload Resume" description="Share your resume for AI analysis" />
            <Step number="2" title="Select Role" description="Choose your target job position" />
            <Step number="3" title="Take Interview" description="Answer AI-generated questions" />
            <Step number="4" title="Get Report" description="Review detailed performance analysis" />
          </div>
        </div>
      </div>
    </div>
  )
}

function FeatureCard({ icon, title, description }) {
  return (
    <div className="bg-white rounded-xl p-6 shadow-lg hover:shadow-xl transition-shadow">
      <div className="mb-4">{icon}</div>
      <h3 className="text-xl font-semibold text-gray-800 mb-2">{title}</h3>
      <p className="text-gray-600">{description}</p>
    </div>
  )
}

function Step({ number, title, description }) {
  return (
    <div className="text-center">
      <div className="w-12 h-12 bg-blue-600 text-white rounded-full flex items-center justify-center text-xl font-bold mx-auto mb-3">
        {number}
      </div>
      <h4 className="font-semibold text-gray-800 mb-1">{title}</h4>
      <p className="text-sm text-gray-600">{description}</p>
    </div>
  )
}

export default HomePage
