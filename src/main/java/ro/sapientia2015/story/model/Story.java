package ro.sapientia2015.story.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import ro.sapientia2015.task.model.Task;

import java.util.List;

import javax.persistence.*;

/**
 * @author Kiss Tibor
 */
@Entity
@Table(name="story")
public class Story {

    public static final int MAX_LENGTH_DESCRIPTION = 500;
    public static final int MAX_LENGTH_TITLE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "creation_time", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime creationTime;

    @Column(name = "description", nullable = true, length = MAX_LENGTH_DESCRIPTION)
    private String description;

    @Column(name = "modification_time", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime modificationTime;

    @Column(name = "title", nullable = false, length = MAX_LENGTH_TITLE)
    private String title;
    
    @Column(name = "tasks")
	@OneToMany(mappedBy = "story")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Task> tasks;

    @Version
    private long version;

    public Story() {

    }

    public static Builder getBuilder(String title) {
        return new Builder(title);
    }

    public Long getId() {
        return id;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getModificationTime() {
        return modificationTime;
    }

    public String getTitle() {
        return title;
    }
    
    public List<Task> getTasks() {
    	return tasks;
    }

    public long getVersion() {
        return version;
    }
    
    public void addTask(Task task) {
    	tasks.add(task);
    }

    @PrePersist
    public void prePersist() {
        DateTime now = DateTime.now();
        creationTime = now;
        modificationTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        modificationTime = DateTime.now();
    }
    
	public void updateTask(Task task) {
		if (tasks != null) {
			for (Task t : tasks) {
				if (task.getId() == t.getId()) {
					tasks.remove(t);
					tasks.add(task);
					return;
				}
			}
		}
	}

    public void update(String description, String title) {
        this.description = description;
        this.title = title;
    }
    
    public void update(String description, String title, List<Task> tasks) {
    	System.out.println(">>> UPDATE STORY:"
    			+ "\n | title:          " + title
    			+ "\n | desc:        " + description
    			+ "\n | tasks size: " + tasks.size()
    			//+ "\n | tasks:     " + (model.getTasks() != null?"":"null"
    			);
        this.description = description;
        this.title = title;
        this.tasks = tasks;
    }

    public static class Builder {

        private Story built;

        public Builder(String title) {
            built = new Story();
            built.title = title;
        }

        public Story build() {
            return built;
        }

        public Builder description(String description) {
            built.description = description;
            return this;
        }
        
        public Builder tasks(List<Task> tasks) {
            built.tasks = tasks;
            return this;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
