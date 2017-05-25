/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import model.DAO.CourseDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The Section class represents a section and captures the course, section, day,
 * start time, end time, instructor, venue, along with the size of the section
 */
public class Section {

    private String course;
    private String section;
    private int day;
    private String start;
    private String end;
    private String instructor;
    private String venue;
    private int size;

    /**
     *
     * Constructs an empty Section object
     */
    public Section() {

    }

    /**
     *
     *
     * Constructs a Section object with the specified course code, section code,
     * day, start time, end time, instructor name, venue, and size
     *
     * @param course course code of the section
     * @param section section code of the section
     * @param day day of the section
     * @param start start time of the section
     * @param end end time of the section
     * @param instructor instructor name teaching the section
     * @param venue venue of the section
     * @param size size of the section
     */
    public Section(String course, String section, int day, String start, String end, String instructor, String venue, int size) {
        this.course = course;
        this.section = section;
        this.day = day;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.venue = venue;
        this.size = size;
    }

    /**
     * Returns the course of the section
     *
     * @return the course code
     */
    public String getCourse() {
        return course;
    }

    /**
     * Returns the section code of the section
     *
     * @return the section code
     */
    public String getSection() {
        return section;
    }

    /**
     * Returns the day of the section
     *
     * @return the day of the section
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the start time of the section
     *
     * @return the start time of the section
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the end time of the section
     *
     * @return the end time of the section
     */
    public String getEnd() {
        return end;
    }

    /**
     * Returns the instructor who is teaching the section
     *
     * @return the instructor who is teaching the section
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Returns the venue section is held at
     *
     * @return the venue section is held at
     */
    public String getVenue() {
        return venue;
    }

    /**
     * Returns the size of the section
     *
     * @return the size of the section
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the course code which section belongs to
     *
     * @param course course code of the section
     * @throws RuntimeException if course does not exist
     */
    public void setCourse(String course) throws RuntimeException {
        if (CourseDAO.getCourse(course) == null) {
            throw new RuntimeException("invalid course");
        }

        this.course = course;
    }

    /**
     * Sets the section code which section belongs to
     *
     * @param section code of the section
     * @throws RuntimeException if section code does not start with 'Sn' where n
     * represents a digit within the range 1-99
     */
    public void setSection(String section) throws RuntimeException {
        if (section.charAt(0) == 'S') {
            try {
                int sectionNo = Integer.parseInt(section.substring(1));
                if (sectionNo > 0 && sectionNo <= 99) {
                    this.section = section;
                } else {
                    throw new RuntimeException("invalid section");
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("invalid section");
            }
        } else {
            throw new RuntimeException("invalid section");
        }
    }

    /**
     * Sets the day of the section
     *
     * @param day day of the section
     * @throws RuntimeException if the the String day does not contain a
     * parsable integer or the parsable integer is not within range of 1-7
     */
    public void setDay(String day) throws RuntimeException {
        try {
            int checkedDay = Integer.parseInt(day);
            if (checkedDay >= 1 && checkedDay <= 7) {
                this.day = checkedDay;
            } else {
                throw new RuntimeException("invalid day");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid day");
        }
    }

    /**
     * Sets the start time of the section
     *
     * @param start start time of the section
     * @throws RuntimeException if the start time String is not in the format
     * "H:mm"
     */
    public void setStart(String start) throws RuntimeException {
        if (start.length() > 5) {
            throw new RuntimeException("invalid start");
        } else if(!start.contains(":")) {
            throw new RuntimeException("invalid start");
        }
        String tmpA = start.substring(0,start.indexOf(":"));
        String tmpB = start.substring(start.indexOf(":")+1);
        try {
            int hour = Integer.parseInt(tmpA);
            int min = Integer.parseInt(tmpB);
            if (!(hour >=0 && hour < 24)) {
                throw new RuntimeException("invalid start");
            }
            if(!(min >=0 && min < 60)) {
                throw new RuntimeException("invalid start");
            }
            this.start = start;
        } catch(NumberFormatException e) {
            throw new RuntimeException("invalid start");
        }
    }

    /**
     * Sets the end time of the section
     *
     * @param end end time of the section
     * @throws RuntimeException if the end time String is not in the format
     * "H:mm"
     */
    public void setEnd(String end) throws RuntimeException {
        if (end.length() > 5) {
            throw new RuntimeException("invalid end");
        } else if(!end.contains(":")) {
            throw new RuntimeException("invalid end");
        }
        String tmpA = end.substring(0,end.indexOf(":"));
        String tmpB = end.substring(end.indexOf(":")+1);
        try {
            int hour = Integer.parseInt(tmpA);
            int min = Integer.parseInt(tmpB);
            if (!(hour >=0 && hour < 24)) {
                throw new RuntimeException("invalid end");
            }
            if(!(min >=0 && min < 60)) {
                throw new RuntimeException("invalid end");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (start != null) {
                Date checkedStart = sdf.parse(start);
                Date checkedEnd = sdf.parse(end);
                if (checkedEnd.after(checkedStart)) {
                    this.end = end;
                } else {
                    throw new RuntimeException("invalid end");
                }

            }
        } catch(NumberFormatException e) {
            throw new RuntimeException("invalid end");
        } catch(ParseException e) {
            throw new RuntimeException("invalid end");
        }
    }

    /**
     * Sets the instructor who is teaching the section
     *
     * @param instructor name of instructor who is teaching the section
     * @throws RuntimeException if the name of the instructor is more than 100
     * characters
     */
    public void setInstructor(String instructor) throws RuntimeException {
        if (instructor.length() > 100) {
            throw new RuntimeException("invalid instructor");
        }
        this.instructor = instructor;
    }

    /**
     * Sets the venue of the section
     *
     * @param venue venue of the section
     * @throws RuntimeException if the name of the venue is more than 100
     * characters
     */
    public void setVenue(String venue) throws RuntimeException {
        if (venue.length() > 100) {
            throw new RuntimeException("invalid venue");
        }
        this.venue = venue;
    }

    /**
     * Sets the size of the section
     *
     * @param size size of the section
     * @throws RuntimeException if the String size does not contain a parsable
     * integer or parsed integer is not more than 0
     */
    public void setSize(String size) throws RuntimeException {
        try {
            int checkedSize = Integer.parseInt(size);
            if (checkedSize > 0) {
                this.size = checkedSize;
            } else {
                throw new RuntimeException("invalid size");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid size");
        }
    }

}
