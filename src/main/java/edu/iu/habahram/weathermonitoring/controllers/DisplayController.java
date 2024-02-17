package edu.iu.habahram.weathermonitoring.controllers;

import edu.iu.habahram.weathermonitoring.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/displays")
public class DisplayController {
    private Map<String, Observer> displays;
    private WeatherData weatherData;

    public DisplayController(CurrentConditionDisplay currentConditionDisplay, HeatIndexDisplay heatIndexDisplay, StatisticsDisplay statisticsDisplay, WeatherData weatherData) {
        this.displays = new HashMap<>();
        this.weatherData = weatherData;

        // Add all displays to the map
        addDisplay(currentConditionDisplay);
        addDisplay(heatIndexDisplay);
        addDisplay(statisticsDisplay);

        // Subscribe all displays to weather data
        this.weatherData.subscribe(currentConditionDisplay);
        this.weatherData.subscribe(heatIndexDisplay);
        this.weatherData.subscribe(statisticsDisplay);
    }

    private void addDisplay(Observer display) {
        displays.put(display.id(), display);
    }

    @GetMapping
    public ResponseEntity index() {
        String html = "<h1>Available screens:</h1><ul>";

        // Iterate through displays and generate HTML for each
        for (Observer display : displays.values()) {
            html += String.format("<li><a href=\"/displays/%s\">%s</a></li>", display.id(), display.name());
        }

        html += "</ul>";
        return ResponseEntity.ok(html);
    }

    @GetMapping("/{id}")
    public ResponseEntity display(@PathVariable String id) {
        Observer display = displays.get(id);
        if (display != null && display instanceof DisplayElement) {
            return ResponseEntity.ok(((DisplayElement) display).display());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Display not found");
        }
    }

    @GetMapping("/{id}/subscribe")
    public ResponseEntity subscribe(@PathVariable String id) {
        String html = "";
        HttpStatus status;
        Observer display = displays.get(id);
        if (display != null) {
            // Check if the display is not already subscribed
            if (!weatherData.getObservers().contains(display)) {
                // Subscribe the display to weather data
                weatherData.subscribe(display);
                html = "Subscribed!";
                status = HttpStatus.FOUND;
            } else {
                html = "Display is already subscribed.";
                status = HttpStatus.BAD_REQUEST;
            }
        } else {
            html = "The screen id is invalid.";
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(html);
    }

    @GetMapping("/{id}/unsubscribe")
    public ResponseEntity unsubscribe(@PathVariable String id) {
        String html = "";
        HttpStatus status;
        Observer display = displays.get(id);
        if (display != null) {
            // Check if the display is subscribed
            if (weatherData.getObservers().contains(display)) {
                // Unsubscribe the display from weather data
                weatherData.unsubscribe(display);
                html = "Unsubscribed!";
                status = HttpStatus.FOUND;
            } else {
                html = "Display is not currently subscribed.";
                status = HttpStatus.BAD_REQUEST;
            }
        } else {
            html = "The screen id is invalid.";
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(html);
    }
}
