package fr.simon.marquis.preferencesmanager.util

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object Shell {
    suspend fun getShell() = suspendCancellableCoroutine { cont ->
        Shell.getShell {
            cont.resume(it)
        }
    }

    fun cp(src: String, dest: String): Shell.Result {
        return Shell.cmd("""cp "$src" "$dest"""").exec()
    }

    fun chown(file:String, uid: Int, gid: Int = uid): Shell.Result {
        return Shell.cmd("""chown $uid:$gid "$file"""").exec()
    }

    fun cat(file: String): Shell.Job {
        return Shell.cmd("""cat "$file"""")
    }

    fun findXmlFiles(packageName: String): Shell.Job {
        return Shell.cmd("""find /data/data/$packageName -type f -name \*.xml""")
    }
}