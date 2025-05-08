package com.vrs;

import com.github.javafaker.Faker;
import com.vrs.domain.entity.OrganizationDO;
import com.vrs.service.OrganizationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 模拟用户数据生成
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsAdminApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationGenerateTest {
    private final Faker faker = new Faker(Locale.CHINA);

    @Autowired
    private OrganizationService organizationService;

    @Test
    public void generate() {
        List<OrganizationDO> organizationDOList=new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            OrganizationDO organizationDO=new OrganizationDO();
            organizationDO.setName(faker.university().name());
            organizationDO.setMark(faker.regexify("[a-zA-Z]{3,10}"));
            organizationDO.setLogo("111");
            organizationService.save(organizationDO);
        }
        organizationService.saveBatch(organizationDOList);
    }
}
