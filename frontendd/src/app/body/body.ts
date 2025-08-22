import { Component,Input } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-body',
  imports: [CommonModule, RouterModule],
  templateUrl: './body.html',
  styleUrl: './body.scss'
})
export class Body {
  

    @Input() collapsed = false;
    @Input() screenWidth = 0;
  
    getBodyClass(): string {
      let styleClass = '';
      if(this.collapsed && this.screenWidth > 768) {
        styleClass = 'body-trimmed';
      } else if(this.collapsed && this.screenWidth <= 768 && this.screenWidth > 0) {
        styleClass = 'body-md-screen'
      }
      return styleClass;
    }
  
}


