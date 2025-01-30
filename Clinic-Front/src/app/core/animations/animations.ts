import { trigger, style, animate, transition } from '@angular/animations';

export const fadeAnimation = trigger('fadeAnimation', [
  transition(':enter', [
    style({ opacity: 0, transform: 'translateY(10px)' }),
    animate('0.3s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
  ])
]);

export const slideAnimation = trigger('slideAnimation', [
  transition(':enter', [
    style({ transform: 'translateX(-100%)' }),
    animate('0.5s ease-out', style({ transform: 'translateX(0)' }))
  ])
]);
