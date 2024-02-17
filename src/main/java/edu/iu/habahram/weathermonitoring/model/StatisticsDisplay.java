package edu.iu.habahram.weathermonitoring.model;

import org.springframework.stereotype.Component;

import java.util.Queue;

@Component
public class StatisticsDisplay implements Observer, DisplayElement {

    private Queue<Float> tempQueue;
    private float avgTemp;
    private float minTemp;
    private float maxTemp;
    private Subject weatherData;

    public StatisticsDisplay(Subject weatherData) {
        this.weatherData = weatherData;
    }
    @Override
    public String display() {
        String html = "";
        html += String.format("<div style=\"background-image: " +
                "url(/images/sky.webp); " +
                "height: 400px; " +
                "width: 647.2px;" +
                "display:flex;flex-wrap:wrap;justify-content:center;align-content:center;" +
                "\">");
        html += "<section>";
        html += String.format("<label>Average Temperature: %s</label><br />", avgTemp);
        html += String.format("<label>Minimum Temperature: %s</label><br />", minTemp);
        html += String.format("<label>Maximum Temperature: %s</label>", maxTemp);
        html += "</section>";
        html += "</div>";
        return html;
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        // Update the temperature queue
        tempQueue.offer(temperature);
        if (tempQueue.size() > 10) {
            tempQueue.poll();
        }

        // Calculate average, min, and max temperatures
        float sum = 0;
        minTemp = Float.MAX_VALUE;
        maxTemp = Float.MIN_VALUE;

        for (float temp : tempQueue) {
            sum += temp;
            minTemp = Math.min(minTemp, temp);
            maxTemp = Math.max(maxTemp, temp);
        }

        avgTemp = sum / tempQueue.size();
    }

    @Override
    public String name() {
        return "Stats Display";
    }

    @Override
    public String id() {
        return "weather-stats";
    }
}
