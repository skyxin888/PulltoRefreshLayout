package cn.xin.pulltorefreshlibrary;

public abstract class PulltorefreshRefreshListener {
    public void onfinish(){};
    public abstract void onRefresh(PulltoRefreshLayout pulltoRefreshLayout);
    public abstract void onRefreshLoadMore(PulltoRefreshLayout pulltoRefreshLayout);
}
