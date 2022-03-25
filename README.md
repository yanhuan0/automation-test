* 从huwang/automation-test项目fork一个自己的远程项目
* 本地clone远程项目：
<pre>
git clone ssh://git@git.yanwuting.cn:2222/<namespace>/automation-test.git
</pre>
* 添加源项目路径为upstream：
<pre>
git remote add upstream ssh://git@git.yanwuting.cn:2222/huwang/automation-test.git
</pre>
* 查看源路径：
<pre>
git remote -v
</pre>
* 从upstream 合并最新代码（rebase）：
git rebase upstream/master
</pre>
* 本地修改代码，提交到自己的远程项目：
<pre>
git add <file>
git commit -m “message
git show //确认一下更改的代码
git push origin master
</pre>
* 到git仓库上提交Merge Request, 选择想合并的分支和需要评审的人
* 评审通过后，评审者合并到huwang/automation-test 
