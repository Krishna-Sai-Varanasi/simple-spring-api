# Creating Helm repo chart for simple-spring-api

Create the helm chart

```bash
helm create simple-spring-api
```

After creating the chart edit `chart.yaml`
```yaml
apiVersion: v2
name: simple-spring-api
description: A Helm chart for simple-spring-api built using JDK 21 and Spring Boot
type: application
version: 0.1.0
appVersion: "1.16.0"
```

Install the chart locally
```bash
helm install simple-spring-api ./simple-spring-api/
```

Verify it is running
```bash
kubectl get --namespace default svc -w simple-spring-api
```