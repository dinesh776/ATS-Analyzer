// Theme switching functionality
const themeToggle = document.getElementById('themeToggle');
const themeIcon = document.getElementById('themeIcon');

function setTheme(theme) {
    document.documentElement.setAttribute('data-bs-theme', theme);
    localStorage.setItem('theme', theme);
    themeIcon.className = theme === 'dark' ? 'bi bi-moon-fill' : 'bi bi-sun-fill';
}

// Initialize theme
const savedTheme = localStorage.getItem('theme') || 'light';
setTheme(savedTheme);

themeToggle.addEventListener('click', () => {
    const currentTheme = document.documentElement.getAttribute('data-bs-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    setTheme(newTheme);
});

document.getElementById('resumeForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const fileInput = document.getElementById('resumeFile');
    const file = fileInput.files[0];

    if (!file) {
        alert('Please select a PDF file.');
        return;
    }

    if (file.type !== 'application/pdf') {
        alert('Only PDF files are supported.');
        return;
    }

    const jobDescription = document.getElementById('jobDescription').value;

    document.getElementById('loadingIndicator').classList.remove('hidden');
    document.getElementById('resultSection').classList.add('hidden');

    const formData = new FormData();
    formData.append('file', file);

    try {
        const BASE_URL=document.getElementById("BaseUrl_Script").innerText;
        let endpoint = `${BASE_URL}/api/resume/analyze`;
        if (jobDescription.trim()) {
            endpoint = `${BASE_URL}/api/resume/analyze-with-job`;
            formData.append('jobDescription', jobDescription);
        }

        const response = await fetch(endpoint, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Failed to analyze resume.');
        }

        const result = await response.json();
        if(result.score===0){
            Swal.fire({
                title: result.recommendation,
                icon: "error"
            });
        }
        else{
            displayResults(result);
        }


    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred while analyzing the resume.');
    } finally {
        document.getElementById('loadingIndicator').classList.add('hidden');
    }
});

function displayResults(data) {
    // Draw circular progress animation
    const canvas = document.getElementById('scoreCanvas');
    const ctx = canvas.getContext('2d');
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const radius = 80;

    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Draw background circle
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI);
    ctx.strokeStyle = '#e6e6e6';
    ctx.lineWidth = 15;
    ctx.stroke();

    // Draw score progress
    const scorePercent = data.score / 100;
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, -Math.PI / 2, (2 * Math.PI * scorePercent) - Math.PI / 2);
    const gradient = ctx.createLinearGradient(0, 0, canvas.width, 0);
    gradient.addColorStop(0, '#667eea');
    gradient.addColorStop(1, '#764ba2');
    ctx.strokeStyle = gradient;
    ctx.lineWidth = 15;
    ctx.stroke();

    // Animate score counter
    const scoreDisplay = document.getElementById('scoreDisplay');
    let currentScore = 0;
    const duration = 1500;
    const startTime = performance.now();

    function updateScore(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        currentScore = Math.round(data.score * progress);
        scoreDisplay.textContent = currentScore;
        if (progress < 1) {
            requestAnimationFrame(updateScore);
        }
    }
    requestAnimationFrame(updateScore);

    // Update category scores
    const categories = {
        format: { element: 'formatScore', progress: 'formatProgress', max: 20 },
        keywords: { element: 'keywordsScore', progress: 'keywordsProgress', max: 30 },
        achievements: { element: 'achievementsScore', progress: 'achievementsProgress', max: 20 },
        skills: { element: 'skillsScore', progress: 'skillsProgress', max: 30 }
    };

    // Handle category scores
    Object.entries(categories).forEach(([key, config]) => {
        const score = data.categoryScores[key] || 0;
        const element = document.getElementById(config.element);
        const progressBar = document.getElementById(config.progress);

        // Update score text
        element.textContent = `${score}/${config.max}`;

        // Update progress bar
        const percentage = (score / config.max) * 100;
        progressBar.style.width = `${percentage}%`;
        progressBar.setAttribute('aria-valuenow', score);
    });

    // Update recommendation
    const recommendationElement = document.getElementById('recommendation');
    if (recommendationElement && data.recommendation) {
        recommendationElement.textContent = data.recommendation;
    }

    // Update strengths
    const strengthsList = document.getElementById('strengths');
    strengthsList.innerHTML = '';
    if (data.strengths && Array.isArray(data.strengths)) {
        data.strengths.forEach(strength => {
            const li = document.createElement('li');
            li.textContent = strength;
            strengthsList.appendChild(li);
        });
    }

    // Update weaknesses
    const weaknessesList = document.getElementById('weaknesses');
    weaknessesList.innerHTML = '';
    if (data.weaknesses && Array.isArray(data.weaknesses)) {
        data.weaknesses.forEach(weakness => {
            const li = document.createElement('li');
            li.textContent = weakness;
            weaknessesList.appendChild(li);
        });
    }

    // Show results section and scroll to it
    const resultSection = document.getElementById('resultSection');
    resultSection.classList.remove('hidden');
    resultSection.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    });
}



// Settings handling
document.addEventListener('DOMContentLoaded', function() {
    const settingsModal = new bootstrap.Modal(document.getElementById('settingsModal'));
    const settingsToggle = document.getElementById('settingsToggle');

    // Event listeners
    settingsToggle.addEventListener('click', () => {
        settingsModal.show();
    });

});

