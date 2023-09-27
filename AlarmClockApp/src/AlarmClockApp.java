import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.Duration; // Added import for Duration
import java.time.LocalDateTime; // Added import for LocalDateTime
import java.time.format.DateTimeFormatter; // Added import for DateTimeFormatter
import java.time.format.DateTimeParseException; // Added import for DateTimeParseException
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.*;
import java.io.File;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import java.io.FileInputStream;
import java.io.IOException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javax.swing.JOptionPane;

public class AlarmClockApp {
    private static JLabel clockLabel = new JLabel(); // Initialize clockLabel
    private static JLabel stopwatchLabel = new JLabel("Stopwatch: 0 seconds"); // Initialize stopwatchLabel
    private static Timer timer;
    private static TimerTask stopwatchTask;
    private static int stopwatchSeconds = 0;




    public static void main(String[] args) {
        JFrame frame = new JFrame("Alarm Clock App");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        JPanel clockPanel = new JPanel(new GridLayout(2, 1));
        clockPanel.add(clockLabel);
        clockPanel.add(stopwatchLabel);
        frame.add(clockPanel);



        // Specific Date and Time Alarm
        JButton dateAlarmButton = new JButton("Set Date Alarm");
        frame.add(dateAlarmButton);

        // Timeout Alarm
        JButton timeoutAlarmButton = new JButton("Set Timeout Alarm");
        frame.add(timeoutAlarmButton);

        // Stopwatch
        JButton startStopwatchButton = new JButton("Start Stopwatch");
        JButton stopStopwatchButton = new JButton("Stop Stopwatch");
        frame.add(startStopwatchButton);
        frame.add(stopStopwatchButton);

        frame.setVisible(true);

        // Update the clock every second
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateClock();
            }
        }, 0, 1000);

        dateAlarmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a dialog to input the date and time
                String dateTimeInput = JOptionPane.showInputDialog("Enter date and time (e.g., '27 November 2023, 5:23 PM'):");

                // Check if user canceled the input or left it blank
                if (dateTimeInput == null || dateTimeInput.trim().isEmpty()) {
                    return; // User canceled or left it blank
                }

                try {
                    // Parse the user input into a DateTimeFormatter
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, h:mm a", Locale.ENGLISH);
                    LocalDateTime targetDateTime = LocalDateTime.parse(dateTimeInput, formatter);

                    // Calculate the time until the alarm goes off
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    Duration duration = Duration.between(currentDateTime, targetDateTime);

                    // Convert the duration to seconds
                    long secondsUntilAlarm = duration.getSeconds();

                    // Schedule a TimerTask to execute when the alarm goes off
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Perform actions when the alarm goes off
                            String mp3FilePath = "Alarmsounds/Jeno Tomari Kaachhe  Lockdown Release  Somlata  Sudhu Tomari Jonne  Somlata And The Aces.mp3";
                            //JOptionPane.showMessageDialog(null, mp3FilePath, "Message", JOptionPane.INFORMATION_MESSAGE);
                            playMP3Sound(mp3FilePath);
                          //System.out.println("Sound File Path: " + System.getProperty("user.dir"));
                            //playSound("Alarmsounds/your-alarm-sound.wav");
                            JOptionPane.showMessageDialog(null, "Alarm! It's time!", "Alarm", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }, secondsUntilAlarm * 1000); // Convert seconds to milliseconds

                    // Inform the user that the alarm is set
                    JOptionPane.showMessageDialog(null, "Alarm set for " + dateTimeInput, "Alarm Set", JOptionPane.INFORMATION_MESSAGE);
                } catch (DateTimeParseException ex) {
                    // Handle invalid date and time format
                    JOptionPane.showMessageDialog(null, "Invalid date and time format. Please use 'd MMMM yyyy, h:mm a'.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        timeoutAlarmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a dialog to input the timeout duration in seconds
                String timeoutInput = JOptionPane.showInputDialog("Enter timeout duration (in seconds):");

                // Check if user canceled the input or left it blank
                if (timeoutInput == null || timeoutInput.trim().isEmpty()) {
                    return; // User canceled or left it blank
                }

                try {
                    // Parse the user input into an integer representing seconds
                    int timeoutSeconds = Integer.parseInt(timeoutInput);

                    // Calculate the time until the alarm goes off
                    long millisecondsUntilAlarm = timeoutSeconds * 1000;

                    // Schedule a TimerTask to execute when the alarm goes off
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Perform actions when the alarm goes off
                            JOptionPane.showMessageDialog(null, "Timeout Alarm! " + timeoutSeconds + " seconds have passed.", "Alarm", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }, millisecondsUntilAlarm);

                    // Inform the user that the timeout alarm is set
                    JOptionPane.showMessageDialog(null, "Timeout Alarm set for " + timeoutSeconds + " seconds.", "Alarm Set", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    // Handle invalid input (non-integer)
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number of seconds.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        startStopwatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start the stopwatch
                stopwatchTask = new TimerTask() {
                    @Override
                    public void run() {
                        stopwatchSeconds++;
                        // Update stopwatch display
                        // You can use the clockLabel to display the stopwatch time
                        updateStopwatchDisplay();
                    }
                };
                timer.scheduleAtFixedRate(stopwatchTask, 0, 1000);
            }
        });

        stopStopwatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop the stopwatch
                stopwatchTask.cancel();
                // Reset stopwatchSeconds to 0
                stopwatchSeconds = 0;
                // Update stopwatch display
                updateStopwatchDisplay();
            }
        });
    }
    // Define a method to update the stopwatch display
    private static void updateStopwatchDisplay() {
        stopwatchLabel.setText("Stopwatch: " + stopwatchSeconds + " seconds");
    }
    private static void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a");
        Date now = new Date();
        clockLabel.setText(sdf.format(now));
    }
    // Define a method to play an MP3 sound
    private static void playMP3Sound(String mp3FilePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(mp3FilePath);
            Bitstream bitstream = new Bitstream(fileInputStream);
            javazoom.jl.player.advanced.AdvancedPlayer player = new AdvancedPlayer(fileInputStream);


            Thread playerThread = new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            });

            playerThread.start();

            // Close the bitstream and file input stream when playback is finished
            playerThread.join();
            bitstream.close();
            fileInputStream.close();
        } catch (IOException | JavaLayerException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
