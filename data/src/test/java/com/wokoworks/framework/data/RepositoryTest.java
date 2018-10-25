package com.wokoworks.framework.data;

import com.wokoworks.framework.test.data.BaseRepositoryTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestApplication.class)
public abstract class RepositoryTest extends BaseRepositoryTest {

}
