package com.vrs;

import com.vrs.domain.entity.UserDO;
import com.vrs.service.UserService;
import com.vrs.utils.TxtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * 模拟用户数据生成
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsAdminApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTokenCSVGenerateTest {

    @Autowired
    private UserService userService;

    /**
     * csv地址
     */
    private final String csvPath = Paths.get("").toAbsolutePath().getParent().getParent() + File.separator + "tmp" + File.separator + "用户token.csv";

    @Test
    public void generate() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("token\n");
        List<UserDO> userDOList = userService.list();
        for (UserDO userDO : userDOList) {
            // 登录并返回一个token
            stringBuilder.append(userService.handleLogin(userDO).getToken() + "\n");
        }
        TxtUtil.write(new File(csvPath), stringBuilder.toString(), "utf-8");
    }
}
