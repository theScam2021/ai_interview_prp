#!/bin/bash

echo "Creating sample resume..."

# Create HTML resume
cat > sample-resume.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }
        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
        h2 { color: #34495e; margin-top: 25px; border-bottom: 1px solid #bdc3c7; }
        .contact { color: #7f8c8d; margin-bottom: 20px; }
        .section { margin-bottom: 20px; }
        ul { margin: 10px 0; }
        li { margin: 5px 0; }
        .project { margin: 15px 0; }
        .project-title { font-weight: bold; color: #2980b9; }
    </style>
</head>
<body>
    <h1>John Doe</h1>
    <div class="contact">
        john.doe@email.com | (555) 123-4567 | linkedin.com/in/johndoe
    </div>

    <h2>PROFESSIONAL SUMMARY</h2>
    <div class="section">
        Experienced Software Developer with 4+ years of expertise in full-stack development.
        Proficient in Java, Spring Boot, React, and cloud technologies. Strong problem-solving
        skills and proven track record of delivering scalable applications.
    </div>

    <h2>TECHNICAL SKILLS</h2>
    <div class="section">
        <ul>
            <li><strong>Programming Languages:</strong> Java, JavaScript, TypeScript, Python, SQL</li>
            <li><strong>Frameworks & Libraries:</strong> Spring Boot, React, Node.js, Express, Hibernate</li>
            <li><strong>Databases:</strong> MySQL, PostgreSQL, MongoDB, Redis</li>
            <li><strong>Tools & Technologies:</strong> Git, Docker, Kubernetes, Jenkins, Maven, AWS</li>
            <li><strong>Methodologies:</strong> Agile, Scrum, Test-Driven Development, CI/CD</li>
        </ul>
    </div>

    <h2>PROFESSIONAL EXPERIENCE</h2>
    <div class="section">
        <div style="margin-bottom: 20px;">
            <strong>Senior Software Developer</strong> | Tech Solutions Inc. | Jan 2021 - Present
            <ul>
                <li>Developed and maintained microservices architecture using Spring Boot and Docker</li>
                <li>Built responsive web applications with React and TypeScript</li>
                <li>Implemented RESTful APIs serving 100,000+ daily requests</li>
                <li>Optimized database queries reducing response time by 40%</li>
                <li>Mentored 3 junior developers and conducted code reviews</li>
                <li>Implemented CI/CD pipelines using Jenkins and GitLab</li>
            </ul>
        </div>
        
        <div>
            <strong>Software Developer</strong> | StartupXYZ | Jun 2019 - Dec 2020
            <ul>
                <li>Developed full-stack features for SaaS platform</li>
                <li>Created REST APIs using Node.js and Express</li>
                <li>Worked with MongoDB for data persistence</li>
                <li>Participated in agile ceremonies and sprint planning</li>
                <li>Collaborated with cross-functional teams</li>
            </ul>
        </div>
    </div>

    <h2>PROJECTS</h2>
    <div class="section">
        <div class="project">
            <div class="project-title">E-commerce Platform</div>
            <ul>
                <li>Full-stack web application with 10,000+ active users</li>
                <li>Technologies: Java, Spring Boot, React, MySQL, Redis</li>
                <li>Implemented payment gateway integration (Stripe, PayPal)</li>
                <li>Built admin dashboard for inventory management</li>
                <li>Deployed on AWS using EC2, RDS, and S3</li>
            </ul>
        </div>
        
        <div class="project">
            <div class="project-title">Real-time Task Management System</div>
            <ul>
                <li>Collaborative project management tool with real-time updates</li>
                <li>Technologies: Node.js, React, WebSocket, MongoDB</li>
                <li>Implemented real-time notifications and chat features</li>
                <li>Integrated with Slack and email notifications</li>
            </ul>
        </div>
        
        <div class="project">
            <div class="project-title">Microservices Architecture Migration</div>
            <ul>
                <li>Led migration from monolithic to microservices architecture</li>
                <li>Technologies: Spring Boot, Docker, Kubernetes, RabbitMQ</li>
                <li>Improved system scalability and reduced deployment time by 60%</li>
            </ul>
        </div>
    </div>

    <h2>EDUCATION</h2>
    <div class="section">
        <strong>Bachelor of Technology in Computer Science</strong><br>
        State University | 2015 - 2019<br>
        GPA: 3.8/4.0<br>
        Relevant Coursework: Data Structures, Algorithms, Database Systems, Web Development
    </div>

    <h2>CERTIFICATIONS</h2>
    <div class="section">
        <ul>
            <li>AWS Certified Developer - Associate</li>
            <li>Oracle Certified Professional, Java SE 11 Developer</li>
            <li>MongoDB Certified Developer</li>
        </ul>
    </div>

    <h2>ACHIEVEMENTS</h2>
    <div class="section">
        <ul>
            <li>Employee of the Quarter - Q3 2022</li>
            <li>Reduced application load time by 50% through optimization</li>
            <li>Successfully delivered 15+ projects on time and within budget</li>
            <li>Contributed to 3 open-source projects on GitHub</li>
        </ul>
    </div>
</body>
</html>
EOF

echo "Sample resume HTML created: sample-resume.html"
echo ""
echo "To convert to PDF, you can:"
echo "1. Open sample-resume.html in a browser and print to PDF"
echo "2. Use wkhtmltopdf: wkhtmltopdf sample-resume.html sample-resume.pdf"
echo "3. Use pandoc: pandoc sample-resume.html -o sample-resume.pdf"
echo ""
echo "Or simply open the HTML file in your browser and use Print > Save as PDF"
