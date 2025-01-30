import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-public-navbar',
  templateUrl: './public-navbar.component.html',
  styleUrl: './public-navbar.component.scss'
})
export class PublicNavbarComponent {
  isMobileMenuOpen = false;
  constructor(private router: Router,) {}

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  openLoginModal() {
    this.router.navigate(['login']);
  }

  openSignupModal() {
    this.router.navigate(['signup']);
  }
}
