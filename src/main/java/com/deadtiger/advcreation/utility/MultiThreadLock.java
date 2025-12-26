package com.deadtiger.advcreation.utility;

public class MultiThreadLock
{
    private boolean locked;

    public MultiThreadLock()
    {
        this.locked = false;
    }

    public void blockingAttemptAtLocking()
    {
       while(!attemptLocking())
       {}
    }

    public boolean attemptLocking()
    {
        if(!this.locked)
        {
            this.locked = true;
            return true;
        }
        return false;
    }

    public void releaseLock()
    {
        this.locked = false;
    }
}
