import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import {AuthService} from "../../../auth/services/auth.service";

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const allowedRoles = route.data['roles'] as Array<string>;
    const userRole = this.authService.getUserRole(); // e.g., 'ADMIN', 'MANAGER', 'USER'

    if (!userRole || !allowedRoles.includes(userRole)) {
      this.router.navigate(['/home']);
      return false;
    }
    return true;
  }
}
