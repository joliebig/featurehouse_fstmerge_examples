

package org.jfree.data.gantt;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;


public class Task implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 1094303785346988894L;
    
    
    private String description;

    
    private TimePeriod duration;
    
    
    private Double percentComplete;

    
    private List subtasks;

    
    public Task(String description, TimePeriod duration) {
        if (description == null) {
            throw new IllegalArgumentException("Null 'description' argument.");
        }
        this.description = description;
        this.duration = duration;
        this.percentComplete = null;
        this.subtasks = new java.util.ArrayList();
    }
    
    
    public Task(String description, Date start, Date end) {
        this(description, new SimpleTimePeriod(start, end));
    }

    
    public String getDescription() {
        return this.description;
    }

    
    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Null 'description' argument.");
        }
        this.description = description;
    }

    
    public TimePeriod getDuration() {
        return this.duration;
    }

    
    public void setDuration(TimePeriod duration) {
        this.duration = duration;
    }
    
    
    public Double getPercentComplete() {
        return this.percentComplete;
    }
    
    
    public void setPercentComplete(Double percent) {
        this.percentComplete = percent;
    }

    
    public void setPercentComplete(double percent) {
        setPercentComplete(new Double(percent));
    }
    
    
    public void addSubtask(Task subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Null 'subtask' argument.");
        }
        this.subtasks.add(subtask);
    }

    
    public void removeSubtask(Task subtask) {
        this.subtasks.remove(subtask);
    }

    
    public int getSubtaskCount() {
        return this.subtasks.size();
    }

    
    public Task getSubtask(int index) {
        return (Task) this.subtasks.get(index);
    }
    
    
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Task)) {
            return false;
        }
        Task that = (Task) object;
        if (!ObjectUtilities.equal(this.description, that.description)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.duration, that.duration)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.percentComplete, 
                that.percentComplete)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.subtasks, that.subtasks)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        Task clone = (Task) super.clone();
        return clone;      
    }

}
