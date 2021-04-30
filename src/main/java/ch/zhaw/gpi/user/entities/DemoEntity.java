package ch.zhaw.gpi.user.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity-Klasse für Demo
 * 
 * @author scep
 */
@Entity(name = "Demo")
public class DemoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long demoId;     
    private String demoName;

    public Long getDemoId() {
        return demoId;
    }

    public void setDemoId(Long demoId) {
        this.demoId = demoId;
    }

    public String getDemoName() {
        return demoName;
    }

    public void setDemoName(String demoName) {
        this.demoName = demoName;
    }
    
    
    
    
}
