import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Code, Palette, BarChart, Briefcase } from 'lucide-react'

const jobRoles = [
  {
    id: 'java-developer',
    title: 'Java Developer',
    icon: <Code className="w-12 h-12" />,
    description: 'Backend development with Java, Spring Boot, and microservices',
    color: 'blue'
  },
  {
    id: 'frontend-developer',
    title: 'Frontend Developer',
    icon: <Palette className="w-12 h-12" />,
    description: 'UI/UX development with React, JavaScript, and modern frameworks',
    color: 'purple'
  },
  {
    id: 'data-analyst',
    title: 'Data Analyst',
    icon: <BarChart className="w-12 h-12" />,
    description: 'Data analysis, SQL, Python, and business intelligence',
    color: 'green'
  },
  {
    id: 'custom',
    title: 'Custom Role',
    icon: <Briefcase className="w-12 h-12" />,
    description: 'Enter your specific job role',
    color: 'orange'
  }
]

function JobSelectionPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const resumeSummary = location.state?.resumeSummary

  const [selectedRole, setSelectedRole] = useState('')
  const [customRole, setCustomRole] = useState('')
  const [showCustomInput, setShowCustomInput] = useState(false)

  if (!resumeSummary) {
    navigate('/upload')
    return null
  }

  const handleRoleSelect = (roleId) => {
    setSelectedRole(roleId)
    setShowCustomInput(roleId === 'custom')
  }

  const handleContinue = () => {
    const jobRole = selectedRole === 'custom' ? customRole : selectedRole
    
    if (!jobRole) {
      alert('Please select or enter a job role')
      return
    }

    navigate('/interview', { 
      state: { 
        resumeSummary, 
        jobRole 
      } 
    })
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4">
      <div className="max-w-5xl mx-auto">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">Select Your Target Role</h1>
          <p className="text-gray-600">Choose the position you're preparing for</p>
        </div>

        <div className="bg-white rounded-xl shadow-lg p-8 mb-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-4">Resume Analysis Summary</h2>
          <div className="grid md:grid-cols-2 gap-4 text-sm">
            <div>
              <p className="font-semibold text-gray-700">Skills:</p>
              <p className="text-gray-600">{resumeSummary.skills?.join(', ') || 'N/A'}</p>
            </div>
            <div>
              <p className="font-semibold text-gray-700">Experience:</p>
              <p className="text-gray-600">{resumeSummary.experience || 'N/A'}</p>
            </div>
          </div>
        </div>

        <div className="grid md:grid-cols-2 gap-6 mb-8">
          {jobRoles.map((role) => (
            <div
              key={role.id}
              onClick={() => handleRoleSelect(role.id)}
              className={`bg-white rounded-xl p-6 cursor-pointer transition-all transform hover:scale-105 ${
                selectedRole === role.id
                  ? `ring-4 ring-${role.color}-500 shadow-xl`
                  : 'shadow-lg hover:shadow-xl'
              }`}
            >
              <div className={`text-${role.color}-600 mb-4`}>{role.icon}</div>
              <h3 className="text-xl font-semibold text-gray-800 mb-2">{role.title}</h3>
              <p className="text-gray-600 text-sm">{role.description}</p>
            </div>
          ))}
        </div>

        {showCustomInput && (
          <div className="bg-white rounded-xl shadow-lg p-6 mb-6">
            <label className="block text-gray-700 font-semibold mb-2">
              Enter Your Job Role
            </label>
            <input
              type="text"
              value={customRole}
              onChange={(e) => setCustomRole(e.target.value)}
              placeholder="e.g., Full Stack Developer, DevOps Engineer"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        )}

        <div className="flex gap-4">
          <button
            onClick={() => navigate('/upload')}
            className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-3 rounded-lg transition-colors"
          >
            Back
          </button>
          <button
            onClick={handleContinue}
            disabled={!selectedRole || (selectedRole === 'custom' && !customRole)}
            className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-lg transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
          >
            Start Interview
          </button>
        </div>
      </div>
    </div>
  )
}

export default JobSelectionPage
