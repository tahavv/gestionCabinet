let currentSlide = 0;
let totalSlides = /*[[${#lists.size(slides)}]]*/ 0;

const track = document.getElementById('carouselTrack');
let autoSlideInterval = null;

function updateSlidePosition() {
    track.style.transform = `translateX(-${currentSlide * 100}%)`;
    for (let i = 0; i < totalSlides; i++) {
        const dot = document.getElementById('dot-' + i);
        if (dot) {
            dot.classList.remove('active');
        }
    }
    const activeDot = document.getElementById('dot-' + currentSlide);
    if (activeDot) {
        activeDot.classList.add('active');
    }
}

function prevSlide() {
    currentSlide = (currentSlide <= 0) ? totalSlides - 1 : currentSlide - 1;
    updateSlidePosition();
}

function nextSlide() {
    currentSlide = (currentSlide >= totalSlides - 1) ? 0 : currentSlide + 1;
    updateSlidePosition();
}

function goToSlide(index) {
    currentSlide = index;
    updateSlidePosition();
}

document.addEventListener('DOMContentLoaded', () => {
    if (totalSlides === 0) {
        let slidesFound = track.children.length;
        if (slidesFound > 0) {
            totalSlides = slidesFound;
        }
    }
    updateSlidePosition();
    autoSlideInterval = setInterval(nextSlide, 5000); // Fixed: Removed space in 5000
});