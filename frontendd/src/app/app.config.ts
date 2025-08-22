
import { 
  ApplicationConfig, 
  provideBrowserGlobalErrorListeners, 
  provideZonelessChangeDetection,
  importProvidersFrom  
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';  
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes), 
    provideClientHydration(withEventReplay()),
    importProvidersFrom(ReactiveFormsModule),
    provideAnimations()
  ]
};