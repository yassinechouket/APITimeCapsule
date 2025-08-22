import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { UserLoginComponent } from './login/login';
import { UserRegisterComponent } from './signup/signup';
import { HttpClientModule } from '@angular/common/http';
import { Sidenav } from './sidenav/sidenav';
import { Body } from './body/body';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

interface sideNavToggle {
  screeWidth: number;
  collapsed: boolean;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    UserLoginComponent, 
    UserRegisterComponent, 
    HttpClientModule,
    Sidenav,
    Body,
    CommonModule // Add CommonModule for *ngIf
  ],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  protected title = 'APITimeCapsule_Angular';

  isSideNavCollapsed = false;
  screenWidth = 0;
  showSideNav = true; // New property to control sidenav visibility

  constructor(private router: Router) {
    // Listen to route changes
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.checkRoute(event.urlAfterRedirects);
      });
  }

  onToggleSideNav(data: sideNavToggle): void {
    this.screenWidth = data.screeWidth;
    this.isSideNavCollapsed = data.collapsed;
  }

  onToggleSidenavFromHeader(): void {
    this.isSideNavCollapsed = !this.isSideNavCollapsed;
   
    this.onToggleSideNav({
      collapsed: this.isSideNavCollapsed,
      screeWidth: this.screenWidth || window.innerWidth
    });
  }

  private checkRoute(url: string): void {
    // Hide sidenav on login and signup pages
    const hideSidenavRoutes = ['/login', '/signup'];
    this.showSideNav = !hideSidenavRoutes.some(route => url.includes(route));
  }
}