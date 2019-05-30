#!groovy

node('master'){
	stage('准备变量'){
		println "准备环境变量"

		work_dir="${WORKSPACE}"
		project_name="${PROJECT_NAME}"
		environment="${ENVIRONMENT}"
		configs_path="${WORKSPACE}/configs"
		REPO_LOCAL="/root/.m2/repository/weig${environment}"
		compile_log_file="${WORKSPACE}/compile_log.txt"
		sh "rm -f ${compile_log_file}"
	}

	stage('获取项目源码'){
		println "获取项目源码"
        dir("${work_dir}") {
            checkout scm
        }
	}


	stage('准备maven构建环境'){

		println "准备maven构建环境"

		sh "cd ${work_dir}"
	}

	stage('构建并发布'){
		println "构建并发布"

		sh "mvn clean compile package -Pweig${environment} -Dmaven.repo.local=${REPO_LOCAL} -Dmaven.test.skip=true -DaltDeploymentRepository=${REPO_REMOTE} > ${compile_log_file} || echo Compile failed"
		compile_result=sh(returnStdout: true, script: "fgrep [ERROR] ${compile_log_file} || echo PASS").trim()
		echo "compile result: ${compile_result}"
		if (!compile_result.equals("PASS")) {
		    sh "exit 1"
        }
	}
	stage('archive artifacts') {
	    sh "cd ${work_dir}/target && cp *.${package_type} ${work_dir}/${project_name}.${package_type}"
	    archiveArtifacts artifacts: "${project_name}*.${package_type}", onlyIfSuccessful: true
	}

}
