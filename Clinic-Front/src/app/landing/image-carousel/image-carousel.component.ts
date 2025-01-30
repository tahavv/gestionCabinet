import {Component, OnDestroy, OnInit} from '@angular/core';

@Component({
  selector: 'app-image-carousel',
  templateUrl: './image-carousel.component.html',
  styleUrl: './image-carousel.component.scss'
})
export class ImageCarouselComponent implements OnInit ,OnDestroy{
  slides = [
    {
      image: './assets/icons/Healthcare1.jpg',
      caption: 'State-of-the-Art Facilities',
      description: 'Experience healthcare in our modern medical center'
    },
    {
      image: './assets/icons/Healthcare3.jpg',
      caption: 'Expert Medical Team',
      description: 'Our experienced doctors provide the best care'
    },
    {
      image: './assets/icons/Healthcare2.jpeg',
      caption: 'Advanced Technology',
      description: 'Using the latest medical technology for better treatment'
    }
  ];
  currentSlide = 0;
  private autoSlideInterval: any;

  ngOnInit() {
    this.startAutoSlide();
  }

  ngOnDestroy() {
    this.stopAutoSlide();
  }

  startAutoSlide() {
    this.autoSlideInterval = setInterval(() => {
      this.nextSlide();
    }, 5000);
  }

  stopAutoSlide() {
    if (this.autoSlideInterval) {
      clearInterval(this.autoSlideInterval);
    }
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide + 1) % this.slides.length;
  }

  prevSlide() {
    this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
  }

  goToSlide(index: number) {
    this.currentSlide = index;
  }
}
