import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../../auth/services/auth.service";

@Component({
  selector: 'app-navbar-admin',
  templateUrl: './navbar-admin.component.html',
  styleUrl: './navbar-admin.component.scss'
})
export class NavbarAdminComponent {
  @Input() clinicName = '';
  @Input() user :any;
  @Input() isOpen = true;
  @Output() toggleSidebar = new EventEmitter<void>();
  showProfileMenu = false;
  constructor(private router: Router, private authService: AuthService) {}


  onToggle(): void {
    this.isOpen = !this.isOpen;
    this.toggleSidebar.emit();
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
  }

  logout() {
    this.authService.logout();
  }
}
