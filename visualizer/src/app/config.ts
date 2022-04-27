import * as L from "leaflet";
import {KeycloakService} from "keycloak-angular";

export const STORAGE_API_URL = 'http://localhost:8000/storage/api'
export const ARCHIVER_API_URL = 'http://localhost:8000/archiver/api'
export const OPEN_STREET_MAP_API_URL_TEMPLATE = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'

export const MARKER_ICON = L.icon({
    iconSize: [25, 41],
    iconAnchor: [10, 41],
    popupAnchor: [2, -40],
    iconUrl: "https://unpkg.com/leaflet@1.5.1/dist/images/marker-icon.png",
    shadowUrl: "https://unpkg.com/leaflet@1.5.1/dist/images/marker-shadow.png"
});

export function initializeKeycloak(keycloak: KeycloakService) {
    return () =>
        keycloak.init({
            config: {
                url: 'http://localhost:8000/',
                realm: 'location-tracker',
                clientId: 'visualizer',
            },
            initOptions: {
                onLoad: 'login-required',
                checkLoginIframe: true
            }
        });
}